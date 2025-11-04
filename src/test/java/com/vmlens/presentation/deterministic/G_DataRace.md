# Data Races

* The compiler, the cpu may reorder statements if not correctly synchronized.
* A not correctly synchronized field, static field or array access is called a data race.
* For our unit tests we check if all accesses are correctly synchronized