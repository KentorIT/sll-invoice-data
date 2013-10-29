#!/bin/bash
#
# Reads a text file with item and price on each row and creates
# a corresponding JSON structure.
# Input file name is given as an argument and shall be named (basename) as the supplier id.
# Validfrom date has to be edited by hand, and so also trailing commas
#
# record syntax:
#
# [itemId][space][price]
#
# 
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

