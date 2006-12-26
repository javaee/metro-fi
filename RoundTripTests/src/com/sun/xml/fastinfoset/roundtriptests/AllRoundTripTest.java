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

package com.sun.xml.fastinfoset.roundtriptests;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Alexey Stashok
 */
public class AllRoundTripTest {
    
    private RoundTripRtt[] roundTripTests = new RoundTripRtt[] {
        new SAXRoundTripRtt(), new StAXRoundTripRtt(), new DOMRoundTripRtt(),
        new DOMSAXRoundTripRtt(), new SAXStAXDiffRtt()};
    
    public void processAllRttTests(String testSrc, String report) {
        File testSrcFile = new File(testSrc);
        File reportFile = new File(report);
        for (int i=0; i<roundTripTests.length; i++) {
            RoundTripTestExecutor roundTripTest = new RoundTripTestExecutor(roundTripTests[i], reportFile);
            roundTripTest.processFileOrFolder(testSrcFile);
        }
        
        cleanupDiffs(testSrcFile);
    }
    
    private void cleanupDiffs(File testSrcFile) {
        if (testSrcFile.isFile()) {
            testSrcFile = testSrcFile.getParentFile();
        }
        
        FileUtils.removeFileRecursivelly(testSrcFile, new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return !file.getName().equals(".") && !file.getName().equals("..");
                } else if (file.isFile()) {
                    String filename = file.getName();
                    return filename.endsWith(".finf") || filename.endsWith(".sax-event") ||
                            filename.indexOf(".diff") != -1;
                }
                return false;
            }
        });
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough parameters!!!");
            System.out.println("Use AllRoundTripTest <test_src> <report_filename>");
            System.out.println("Where <test_src> - testing file or folder");
            System.out.println("<report_filename> - file for generating report");
            System.exit(0);
        }
        
        new AllRoundTripTest().processAllRttTests(args[0], args[1]);
    }
}
