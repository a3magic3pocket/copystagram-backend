# copystagram-backend
copystagram-backend

# docker compose command
- ```bash
    # Run containers
    docker compose -f ./docker-compose.yml up

    # Remove containers
    docker compose -f ./docker-compose.yml down
    ```
# The commands of kafka
- ```bash
    # post-creation 토픽 생성
    ./kafka-topics.sh --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic post-creation --create

    # noti 토픽 생성
    ./kafka-topics.sh --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic noti-creation --create

    # 토픽 리스트 조회
    ./kafka-topics.sh --bootstrap-server localhost:9092 --list

    # post-creation 토픽 상세 조회
    ./kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic post-creation

    # post-creation 컨슈머그룹 조회
    ./kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group post-creation

    # post-creation 컨슈머 실행(헤더 포함)
    ./kafka-console-consumer.sh --bootstrap-server localhost:9092 \
        --from-beginning \
        --topic post-creation \
        --property print.headers=true

    ```


    ./kafka-topics.sh --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic post-creation --create
    
    ./kafka-topics.sh --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic noti-creation --create

    ./kafka-topics.sh --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic meta-post --create

    ./kafka-topics.sh --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic meta-post-result --create

    ./kafka-topics.sh --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic meta-post-list --create

    ./kafka-topics.sh --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic meta-post-list-result --create


    ./kafka-streams-application-reset.sh --application-id meta-post \
                                    --input-topics meta-post \
                                    --intermediate-topics meta-post-result


    ./kafka-streams-application-reset.sh --application-id meta-post-list \
                                    --input-topics meta-post-list \
                                    --intermediate-topics meta-post-list-result