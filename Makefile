runPg:
	docker run -p 5432:5432 -e POSTGRES_PASSWORD=password -e PGDATA=~/tmp/pg/ postgres

psql:
	docker exec -it 260520b3d903 bash