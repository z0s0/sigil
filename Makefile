runPg:
	docker run -p 5432:5432 -e POSTGRES_PASSWORD=password -e PGDATA=~/tmp/pg/ postgres

psql:
	docker exec -it 21cc2b1dd252 bash