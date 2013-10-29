#!/bin/bash
#
# Copyright (c) 2013 SLL. <http://sll.se>
#
# This file is part of Invoice-Data.
#
#     Invoice-Data is free software: you can redistribute it and/or modify
#     it under the terms of the GNU Lesser General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     Invoice-Data is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU Lesser General Public License for more details.
#
#     You should have received a copy of the GNU Lesser General Public License
#     along with Invoice-Data.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
#

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

