/**
 * Copyright (C) 2002 Bas Peters (mail@bpeters.com)
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.marc4j.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.marc4j.marcxml.Converter;
import org.marc4j.marcxml.MarcResult;
import org.marc4j.marcxml.MarcXmlHandler;
import org.marc4j.marcxml.SaxErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * <p>
 * Provides a driver for <code>MarcXmlHandler</code>
 * to convert MARCXML to MARC tape format (ISO 2709) either
 * by providing a MARCXML document or by pre-processing a
 * different XML format by using an XSLT stylesheet that
 * outputs a well-formed MARCXML document.
 * </p>
 *
 * <p>
 * For usage, run from the command-line with the following command:
 * </p>
 * <p>
 * <code>java org.marc4j.util.XmlMarcWriter -usage</code>
 * </p>
 *
 * <p>
 * <b>A note about character encodings:</b><br/>
 * If no output encoding is specified on the command-line the
 * default charset is used. To specify an output encoding use a
 * charset name supported by your Java virtual machine. For input
 * MARC4J relies on the encoding in the XML declaration and the
 * underlying SAX2 XML parser implementation.
 * The following command-line example converts UTF-8 to ANSEL:
 * </p>
 * 
 * <pre>
 * java org.marc4j.util.XmlMarcWriter -convert ANSEL -oe ISO8859_1  -out output.mrc input.xml
 * </pre>
 * <p>
 * <b>Note:</b> the Latin-1 encoding (ISO8859_1) is used since ANSEL is not a supported character encoding.
 * </p>
 *
 * <p>
 * <b>Note:</b> this class requires a JAXP compliant SAX2 parser.
 * For W3C XML Schema support a JAXP 1.2 compliant parser is needed.
 * </p>
 *
 * <p>
 * Check the home page for <a href="http://www.loc.gov/standards/marcxml/">
 * MARCXML</a> for more information about the MARCXML format.
 * </p>
 *
 * @author Bas Peters
 * @see MarcXmlHandler
 * @see MarcWriter
 * @see Converter
 */
public class XmlMarcWriter
{

    private static final Logger log = LoggerFactory.getLogger(XmlMarcWriter.class);
    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    public static void main(String[] args)
    {
        String input = null;
        String inputEncoding = null;
        String output = null;
        String outputEncoding = null;
        String stylesheet = null;
        String schemaSource = null;
        String convert = null;
        boolean ansel = false;
        boolean dtdValidate = false;
        boolean xsdValidate = false;
        long start = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if ("-dtd".equals(args[i])) {
                dtdValidate = true;
            } else if ("-xsd".equals(args[i])) {
                xsdValidate = true;
            } else if ("-xsdss".equals(args[i])) {
                if (i == args.length - 1) {
                    XmlMarcWriter.usage();
                }
                xsdValidate = true;
                schemaSource = args[++i];
            } else if ("-out".equals(args[i])) {
                if (i == args.length - 1) {
                    XmlMarcWriter.usage();
                }
                output = args[++i];
            } else if ("-oe".equals(args[i])) {
                if (i == args.length - 1) {
                    XmlMarcWriter.usage();
                }
                outputEncoding = args[++i].trim();
            } else if ("-convert".equals(args[i])) {
                if (i == args.length - 1) {
                    XmlMarcWriter.usage();
                }
                convert = args[++i].trim();
            } else if ("-xsl".equals(args[i])) {
                if (i == args.length - 1) {
                    XmlMarcWriter.usage();
                }
                stylesheet = args[++i];
            } else if ("-usage".equals(args[i])) {
                XmlMarcWriter.usage();
            } else if ("-help".equals(args[i])) {
                XmlMarcWriter.usage();
            } else {
                input = args[i];

                // Must be last arg
                if (i != args.length - 1) {
                    XmlMarcWriter.usage();
                }
            }
        }
        if (input == null) {
            XmlMarcWriter.usage();
        }

        try {
            Writer writer;
            // if (output == null) {
            // if (convert != null)
            // writer = new BufferedWriter(new OutputStreamWriter(System.out, "ISO8859_1"));
            // else
            // writer = new BufferedWriter(new OutputStreamWriter(System.out, "UTF8"));
            // } else {
            // if (convert != null)
            // writer = new BufferedWriter(new OutputStreamWriter(
            // new FileOutputStream(output), "ISO8859_1"));
            // else
            // writer = new BufferedWriter(new OutputStreamWriter(
            // new FileOutputStream(output), "UTF8"));
            // }

            if (output == null && outputEncoding != null) {
                writer = new BufferedWriter(new OutputStreamWriter(System.out, outputEncoding));
            } else if (output != null && outputEncoding == null) {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            } else if (output != null && outputEncoding != null) {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), outputEncoding));
            } else {
                writer = new BufferedWriter(new OutputStreamWriter(System.out));
            }

            MarcWriter handler = new MarcWriter(writer);
            if (convert != null) {
                CharacterConverter charconv = null;
                if ("ANSEL".equals(convert)) {
                    charconv = new UnicodeToAnsel();
                } else if ("ISO5426".equals(convert)) {
                    charconv = new UnicodeToIso5426();
                } else if ("ISO6937".equals(convert)) {
                    charconv = new UnicodeToIso6937();
                } else {
                    System.err.println("Unknown character set");
                    System.exit(1);
                }
                handler.setCharacterConverter(charconv);
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(dtdValidate || xsdValidate);
            SAXParser saxParser = factory.newSAXParser();
            if (xsdValidate) {
                saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            }
            if (schemaSource != null) {
                saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(schemaSource));
            }
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setErrorHandler(new SaxErrorHandler());
            InputSource in = new InputSource(new File(input).toURL().toString());

            Source source = new SAXSource(xmlReader, in);
            Result result = new MarcResult(handler);
            Converter converter = new Converter();
            if (stylesheet != null) {
                Source style = new StreamSource(new File(stylesheet).toURL().toString());
                converter.convert(style, source, result);
            } else {
                converter.convert(source, result);
            }

        } catch (ParserConfigurationException e) {
            log.error("La configuraci�n no es correcta", e);
        } catch (SAXNotSupportedException e) {
            log.error("No se soporta la operación indicada", e);
        } catch (SAXNotRecognizedException e) {
            log.error("Identificador no reconocido", e);
        } catch (Exception e) {
            log.error("Se ha producido un error al convertir el documento MARCXML", e);
        }
        System.err.println("Total time: " + (System.currentTimeMillis() - start) + " miliseconds");
    }

    private static void usage()
    {
        System.err.println("MARC4J version beta 7, Copyright (C) 2002-2003 Bas Peters");
        System.err.println("Usage: org.marc4j.util.XmlMarcWriter [-options] <file.xml>");
        System.err.println("Usage: MarcXmlWriter [-options] <file.xml>");
        System.err.println("       -dtd = DTD validation");
        System.err.println("       -xsd = W3C XML Schema validation: hints in instance document");
        System.err.println("       -xsdss <file> = W3C XML Schema validation using schema source <file>");
        System.err.println("       -xsl <file> = Preprocess XML using XSLT stylesheet <file>");
        System.err.println("       -out <file> = Output using <file>");
        System.err.println("       -oe <encoding> = Output using charset <encoding>");
        System.err.println("       -convert [ANSEL | ISO5426 | ISO6937] = convert from UTF-8");
        System.err.println("          to specified character set");
        System.err.println("       -usage or -help = this message");
        System.err.println("See http://marc4j.tigris.org for more information.");
        System.exit(1);
    }

}
