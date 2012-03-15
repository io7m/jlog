#!/bin/sh -ex

rm -rf doc-out

xmllint \
  --noout \
  --xinclude \
  --schema ext/structural-0.1.0/structural-01.xsd \
  doc/documentation.xml

mkdir doc-out
cd doc-out

saxon \
  -xi:on \
  -xsl:../ext/structural-0.1.0/structural-01-standalone-x20.xsl \
  -s:../doc/documentation.xml

cp ../ext/structural-0.1.0/*.css .
cp ../doc/*.css .

