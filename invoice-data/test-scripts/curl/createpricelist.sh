#!/bin/bash

echo "{"
echo "supplierId: \"$(basename $1)\","
echo "serviceCode: \"01\","
echo "validFrom: \"2013-01-01\","
echo "prices: ["

echo-price() {
    echo "{"
    echo "itemId: \"${1}\","
    echo "price: ${2/,/.}"
    echo "},"
}

cat $1 | while read line
do
    echo-price $line
done

echo "]"
echo "}"

