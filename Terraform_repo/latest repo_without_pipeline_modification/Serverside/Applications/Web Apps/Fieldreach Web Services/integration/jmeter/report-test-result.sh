cat statistics.json | grep errorCount | grep -v 'errorCount\" : 0'
if [ $? -eq "1" ] ;
then
        echo "TEST PASS" ;
else
        echo "TESTS FAILED" >> /dev/stderr ;
fi
