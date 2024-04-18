package com.copystagram.api.oauth;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.copystagram.api.user.User;
import com.copystagram.api.user.UserRepository;
import com.copystagram.api.user.UserRole;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
	public final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		// System.out.println("oAuth2User = " + oAuth2User.getAttributes());
		Map<String, Object> attributes = oAuth2User.getAttributes();

		String provider = userRequest.getClientRegistration().getRegistrationId();

		// 유효성 검사
		validateAttributes(attributes);

		// 유저 획득
		User user = saveNewUser(attributes, provider);

		// Role 할당
		Set<GrantedAuthority> authorities = new LinkedHashSet<>();
		String authority = user.getRole().toString();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + authority));

		// 고유값 키 할당; default = "sub"
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
				.getUserNameAttributeName();

		// newAttribute 할당
		Map<String, Object> newAttributes = new HashMap<String, Object>();
		newAttributes.put(userNameAttributeName, user.get_id());
		newAttributes.put(OAuth2UserKey.NAME.getValue(), user.getName());
		newAttributes.put(OAuth2UserKey.IS_ACTIVE.getValue(), user.getIsActive());

		return new DefaultOAuth2User(authorities, newAttributes, userNameAttributeName);
	}

	private void validateAttributes(Map<String, Object> userInfoAttributes) {
		if (!userInfoAttributes.containsKey("email")) {
			throw new IllegalArgumentException("the response of OAuth provider is not contained email");
		}
	}

	private User saveNewUser(Map<String, Object> attributes, String provider) {
		String email = (String) attributes.get("email");
		String openId = (String) attributes.getOrDefault("sub", "");
		String name = email.split("@")[0];
		String locale = (String) attributes.getOrDefault("locale", "");
		String defaultDescription = "Hello copystagram :)";

		User user = userRepository.findByEmail(email);
		if (user == null) {
			User newUser = new User();
			newUser.setOpenId(openId);
			newUser.setEmail(email);
			newUser.setName(name);
			newUser.setLocale(locale);
			newUser.setProvider(provider);
			newUser.setRole(UserRole.NORMAL);
			newUser.setIsActive(true);
			newUser.setDescription(defaultDescription);
			User createdUser = userRepository.save(newUser);

			return createdUser;
		}

		return user;
	}
}
