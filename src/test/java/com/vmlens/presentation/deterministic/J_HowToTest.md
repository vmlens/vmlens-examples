# How to Test?

* Start and stop all threads inside the while loop
* For one **reading** and one **writing** operation check if the read returns either before or after the write
* For **two writing** operations check after the join if the result contains both writes