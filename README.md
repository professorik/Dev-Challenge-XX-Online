# DEV Challenge XX: Backend Online Round

Implementation of [Online Task Backend | DEV Challenge XX]

# Stack
1. Java 17
2. Spring Boot
3. MongoDB

## Run app
> docker-compose up

## See it works:

> http://localhost:8080/api/v1/:sheet_id

> http://localhost:8080/api/v1/:sheet_id/:cell_id

> http://localhost:8080/api/v1/:sheet_id/:cell_id
Body: {"value":"1.0"}

> http://localhost:8080/api/v1/:sheet_id/:cell_id
Body: {"value":"=SUM(var1,2, 3, MIN(3,1,2,4))+1"}

> http://localhost:8080/api/v1/:sheet_id/:cell_id
Body: {"value":"=AVG(var1,  2, 3,-1,var2)"}

> http://localhost:8080/api/v1/:sheet_id/:cell_id
Body: {"value":"=AVG(var1,  2, 3,-1,var2)"}

> http://localhost:8080/api/v1/:sheet_id/:cell_id
Body: {"value":"=MIN(var1, var2, 3)*2"}

> http://localhost:8080/api/v1/:sheet_id/:cell_id
Body: {"value":"=2*MAX(var1, var2, 3)"}

> http://localhost:8080/api/v1/:sheet_id/:cell_id
Body: {"value":"=EXTERNAL_REF(http://localhost:8080/api/v1/devchallenge-xx/var1) + 1"}

##Implementation details

1) For calculating a formula value, a modified Shunting yard algorithm is used to 
convert the expression from infix to modified postfix (Reverse Polish notation)
notation. Afterward, it's easy to get the value. In fact, algorithm had to 
be modified for the sake of variativity in arguments amount and nested functions.

3) Inherently, the sheet can be described as a directed graph. Hence, to know
in what order to recalculate the dependent variables, topological sorting 
was used.

##Improvements

1) Reparse the dependent variables' formulas decreases performance,
so we can save postfix notation in our DB, but it requires more memory.

2) Instead of freaky `findAllById_SheetIdAndDependsOnContains` we could
save all the dependent variables. But every time we change the formula we
should go through all the related variables and change them. Additionally, we
should create some flag of visibility to be able to use blank variables, e.g.
we create var1=var0+2, and there's no var0 in the DB, therefore var1=2 and 
var0 must be created in the DB to note that var1 depends on var0. In this case,
getting the sheet controversially returns var0, to avoid it, we should also
store the visibility flag for each variable and show only initialized cells.
Surely, such an approach gets the business logic layer complicated.

3) Add the ability to test inside the docker container.

4) Creating a webhook subscription on a given public endpoint by EXTERNAL_REF().

5) Checking for cyclic dependency in external links
##Corner cases

1) Catching division by zero
2) Usage of "empty" variables. For instance, let `var1` be equal to `var2`, and let `var2` be not 
in the DB; then `var1` equals `0`, but in case of changing the value of `var2` to - 
for example - `2` `var1` gets the value of `2`.
3) Catching cyclic dependencies
4) Nested functions

##Tests

They are in the `src/test` folder. You could run them with `mvn test`.
Actually, they are executed during the build, so you can also see the results
in the container's logs.
