# mdb2csv

A cli util for converting MS Access databases (ver 1997-2016) to
CSV. Uses jackcess lib.

## Compilation

Reqs:

* jdk 14
* maven (tested w/ 3.6.3, used for fetching deps only)
* `npm i -g adieu`

Run `make bundle`. Expected result: `target/mdb2csv-x.y.z.jar`.

## Usage

List all tables:

~~~
$ java -jar mdb2csv-0.0.1.jar jet4.mdb
AttributeValues
Calendar
...
~~~

Print a csv:

~~~
$ java -jar mdb2csv-0.0.1.jar jet4.mdb Users
Name,FirstName,LastName,Birthday,Gender
Guest,Guest,Guest,Sat Dec 30 00:00:00 EET 1899,0
Creator,The,Creator,Sat Dec 30 00:00:00 EET 1899,0
...
~~~

Export all tables from a db:

~~~
(IFS=$'\n'; for t in `java -jar mdb2csv-0.0.1.jar jet4.mdb`; do
  echo $t; java -jar target/mdb2csv-0.0.1.jar jet4.mdb "$t" > "table.$t.csv";
done)
~~~

## See also

[Why Java Sucks](https://tech.jonathangardner.net/wiki/Why_Java_Sucks)

## License

MIT.
