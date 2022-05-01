runPg:
	docker run -p 5432:5432 -e POSTGRES_PASSWORD=password -e PGDATA=~/tmp/pg/ postgres

psql:
	docker exec -it 11218297d1de bash

flagr:
	docker run -p 18000:18000 checkr/flagr