# MARC4J-DGB

![GitHub](https://img.shields.io/github/license/Digibis/marc4j-dgb) ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/Digibis/marc4j-dgb/Java%20CI) ![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/Digibis/marc4j-dgb?sort=semver)


The goal of MARC4J is to provide an easy to use Application Programming Interface (API) for working with MARC and MARCXML in Java. MARC stands for MAchine Readable Cataloging and is a widely used exchange format for bibliographic data. MARCXML provides a loss-less conversion between MARC (MARC21 but also other formats like UNIMARC) and XML.

[JavaDoc](https://digibis.github.io/marc4j-dgb/javadoc/index.html?overview-summary.html)

## Background

MARC4J releases beta 6 through beta 8a where based on an event based parser like SAX for XML. The MARC4J project started as James (Java MARC events), but since there is already an open source project called James, the project is renamed to MARC4J to avoid confusion in open source communities.

This fork it's based on the beta 7 and retains the original event based parser.

## About MARC

MARC stands for MAchine Readable Cataloguing. The MARC format is a popular exchange format for bibliographic records. The structure of a MARC record is defined in the ISO 2709:1996 (Format for Information Exchange) standard (or ANSI/NISO Z39.2-1994, available online from NISO). The MARC4J API is not a full implementation of the ISO 2709:1996 standard. The standard is implemented as it is used in the MARC formats.

The most popular MARC formats are MARC21 and UNIMARC. The MARC21 format is maintained by the Library of Congress . If you’re not familiar with MARC21, you might want to read Understanding MARC Bibliographic , “a brief description and tutorial” on the standard. For more information on the MARC21 format, visit the MARC formats home page at the Library of Congress Web site. For more information about UNIMARC visit the UNIMARC Manual .

## About Digibís

[DIGIBÍS](http://www.digibis.com/en/) is an R&D&I software engineering company specialized in software development in the field of information exchange and management on the World Wide Web. Our area of activity mainly focuses on Libraries, Archives, Museums and Documentation Centres.

We also provide a metadata-enriched digitization service which, as our software, strictly adheres to international regulations from [Europeana](https://www.europeana.eu/es), the [W3C](https://www.w3.org/), the [Library of Congress](https://loc.gov/) and other international institutions.

[DIGIBÍS](http://www.digibis.com/en/) is a pioneering Spanish company in the digital sector. Among its clients, it can count public bodies (Ministries, governments of Autonomous Communities, Cities) and private organizations, in Spain and abroad.

## Related resources

* [Format for Information Exchange](http://www.niso.org/standards/resources/Z39-2.pdf)
* [MARC21](http://www.loc.gov/marc/)
* [UNIMARC](http://www.ifla.org/VI/3/p1996-1/sec-uni.htm)
* [MARCXML](http://www.loc.gov/standards/marcxml/)


