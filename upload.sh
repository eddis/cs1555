#!/bin/sh

rm myAuction.tar
tar cvf myAuction.tar myauction myAuction.java
scp myAuction.tar jdg39@unixs.cssd.pitt.edu:~/private/cs1555/group/myAuction.tar
