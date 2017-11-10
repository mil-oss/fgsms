@echo Are you sure? (ctrl-c to quit)
pause
@echo Enter postgres user password
psql -U postgres -f drop.sql
