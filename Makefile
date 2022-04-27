runPg:
	docker run -p 5432:5432 -e POSTGRES_PASSWORD=password -e PGDATA=~/tmp/pg/ postgres

psql:
	docker exec -it bf3d49f62c3d bash

flagr:
	docker run -p 18000:18000 checkr/flagr