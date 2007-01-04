/*
 * Fast Infoset Round Trip Test software ("Software")
 *
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Software is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations.
 *
 *    Sun supports and benefits from the global community of open source
 * developers, and thanks the community for its important contributions and
 * open standards-based technology, which Sun has adopted into many of its
 * products.
 *
 *    Please note that portions of Software may be provided with notices and
 * open source licenses from such communities and third parties that govern the
 * use of those portions, and any licenses granted hereunder do not alter any
 * rights and obligations you may have under such open source licenses,
 * however, the disclaimer of warranty and limitation of liability provisions
 * in this License will apply to all Software in this distribution.
 *
 *    You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 */


package com.sun.xml.fastinfoset.roundtriptests.rtt;

import com.sun.xml.fastinfoset.tools.TransformInputOutput;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 *
 * @author Alexey Stashok
 */
public abstract class RoundTripRtt {
    
    public abstract boolean process(File testFile) throws Exception;
    public abstract String getName();
    
    public void transform(String inputFileName, String outputFileName, TransformInputOutput transformer) throws Exception {
        FileInputStream fis = new FileInputStream(inputFileName);
        FileOutputStream fos = new FileOutputStream(outputFileName);
        try {
            transformer.parse(fis, fos);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
            }
            try {
                fos.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public void transform(String inputFileName, String outputFileName, String workingDirectory, TransformInputOutput transformer) throws Exception {
        FileInputStream fis = new FileInputStream(inputFileName);
        FileOutputStream fos = new FileOutputStream(outputFileName);
        try {
            transformer.parse(fis, fos, workingDirectory);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
            }
            try {
                fos.close();
            } catch (IOException ex) {
            }
        }
    }

    public boolean diffBinary(String fileName1, String fileName2, String diffFileName) throws IOException {
        PrintWriter diffWriter = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(diffFileName))));
        InputStream file1InputStream = null;
        InputStream file2InputStream = null;
        
        try {
            File file1 = new File(fileName1);
            File file2 = new File(fileName2);
            
            file1InputStream = new BufferedInputStream(new FileInputStream(fileName1));
            file2InputStream = new BufferedInputStream(new FileInputStream(fileName2));
            
            boolean passed = true;
            
            if (file1.length() != file2.length()) {
                passed = false;
                diffWriter.println("Result FI documents have different size");
            }
            
            if (file1.length() > file2.length()) {
                // If file2 is longer - exchange input stream refs
                InputStream tmpIn = file1InputStream;
                file1InputStream = file2InputStream;
                file2InputStream = tmpIn;
                String tmpName = fileName1;
                fileName1 = fileName2;
                fileName2 = tmpName;
            }
            
            boolean isFirstWrite = true;
            int byteNumber = 0;
            int ch1 = 0;
            while((ch1 = file1InputStream.read()) != -1) {
                int ch2 = file2InputStream.read();
                if (ch1 != ch2) {
                    if (isFirstWrite) {
                        diffWriter.println("<offset>: " + fileName1 + " | " + fileName2);
                        isFirstWrite = false;
                    }
                    diffWriter.println(Integer.toHexString(byteNumber) + ": " +
                            Integer.toHexString(ch1) + " " +
                            Integer.toHexString(ch2));
                    passed = false;
                }
                byteNumber++;
            }
            
            if (!passed) {
                diffWriter.println("Fast infoset document does not have the same content as the X.finf specification");
            }
            return passed;
        } finally {
            if (file1InputStream != null) {
                try {
                    file1InputStream.close();
                } catch (IOException ex) {
                }
            }
            if (file2InputStream != null) {
                try {
                    file2InputStream.close();
                } catch (IOException ex) {
                }
            }
            
            diffWriter.flush();
            diffWriter.close();
        }
        
    }
    
    public boolean diffText(String fileName1, String fileName2, String diffFileName) throws IOException {
        BufferedReader file1Reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName1)));
        BufferedReader file2Reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName2)));
        PrintWriter diffWriter = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(diffFileName))));
        
        try {
            boolean passed = true;
            String line1 = null;
            int lineNumber = 1;
            while((line1 = file1Reader.readLine()) != null) {
                String line2 = file2Reader.readLine();
                if (line2 == null) {
                    passed = false;
                    printHeader1(diffWriter, fileName1, lineNumber);
                    diffWriter.println(line1);
                    diffWriter.println("...............................");
                    printHeader2(diffWriter, fileName2, lineNumber);
                    diffWriter.println("EOF");
                }
                
                if (!line1.equals(line2)) {
                    passed = false;
                    printHeader1(diffWriter, fileName1, lineNumber);
                    diffWriter.println(line1);
                    printHeader2(diffWriter, fileName2, lineNumber);
                    diffWriter.println(line2);
                }
                
                lineNumber++;
            }
            
            if((line1 = file2Reader.readLine()) != null) {
                passed = false;
                printHeader1(diffWriter, fileName1, lineNumber);
                diffWriter.println("EOF");
                printHeader2(diffWriter, fileName2, lineNumber);
                diffWriter.println(line1);
                diffWriter.println("...............................");
            }
            
            return passed;
        } finally {
            try {
                file1Reader.close();
            } catch (IOException ex) {
            }
            try {
                file2Reader.close();
            } catch (IOException ex) {
            }
            
            diffWriter.flush();
            diffWriter.close();
        }
    }
    
    private static void printHeader1(PrintWriter resultWriter, String fileName, int lineNumber) {
        resultWriter.println("\\\\\\\\\\\\\\\\\\ " + fileName + ":" + lineNumber + " \\\\\\\\\\\\\\\\\\\\\\");
    }
    
    private static void printHeader2(PrintWriter resultWriter, String fileName, int lineNumber) {
        resultWriter.println("///////// " + fileName + ":" + lineNumber + " /////////");
    }
}
