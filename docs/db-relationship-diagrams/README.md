How to generate entity relationship diagrams:

1. Download Graphviz
2. Refer mysql-connector jar file
3. output_directory

java -jar schemaSpy_5.0.0.jar -gv <path_to_GraphViz>\graphviz-2.38\release\ -t mysql -o <output_directory> -host -dp=<mysql-connector-java-xx>.jar localhost -u root -db <some_database> -p <your_password>