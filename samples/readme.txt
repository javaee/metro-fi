FastInfoset (FI) Samples

The samples project is in its early stage and not complete. More samples may be added 
in the near further. Please note that due to the fact that there might be some minor 
modifications to the specification of the encoding of Fast Infoset, FI files for the 
samples are not checked in. The ant targets for those samples that have dependencies 
on FI files, therefore, are set to run XMLToFI target first to generate FI files from 
XML files before the targeted tasks are run. For more details, you may run the JavaDoc 
target (ant doc) to generate JavaDocs under dist/javadoc/ and then browse index.html. 
  
We welcome feedbacks and suggestions on use cases. Please write to users@fi.dev.java.net.

Listed here are samples that have been completed:


1. FI SAX parser 
    The sample parses data/inv1a.finf as specified in the build.xml and handle SAX events to display corresponding XML content.
    To run the sample, go to the samples directory and type:
    ant FISAXParser

2. FI SAX serializer
    Demonstrate the use of FI SAX serializer to transform a XML file into FI document. The sample
takes data/inv1.xml and converts it into data/inv1.finf
    To run the sample, go to the samples directory and type:
    ant FISAXSerializer

    To try the sample with other xml files, edit the build.xml and change the "xml-file" property to 
point to the file you may want to test.

3. FI StAX serializer
    Demonstrate the use of FI StAX serializer to transform a XML file into FI document. The sample
takes data/inv1.xml and converts it into data/inv1.finf
    To run the sample, go to the samples directory and type:
    ant FIStAXSerializer

    To try the sample with other xml files, edit the build.xml and change the "xml-file" property to 
point to the file you may want to test.

4. FI StAX EventReader
    Demonstrate the use of FI StAX EventReader to read FI and XML files. The sample
reads data/inv1a.xml and data/inv1a.finf and displays event types as it goes through
the documents.
    To run the sample, go to the samples directory and type:
    ant EventReader

    To try the sample with other xml files, edit the build.xml and change the "simple-xml-file" and 
 "simple-finf-file" properties to point to the files you may want to test. **Note that due to modifications 
to the specification of the encoding of Fast Infoset, finf files have been removed from the repository. For 
your convinence, an utility called XMLToFI is attached to this target so that when the target is called, 
the xml file specified in property "simple-xml-file" is automatically transformed to create the finf file 
specified in property "simple-finf-file".

5. FI StAX StreamReader
    Demonstrate the use of FI StAX StreamReader to read FI files. The sample reads 
data/inv1.finf and displays contents as it goes through the document.
    To run the sample, go to the samples directory and type:
    ant StreamReader

    Similar to the above EventReader, if you want to run the sample on other files, you would need to
change the "simple-xml-file" and "simple-finf-file" properties to point to the files you may want to test. 
The finf file specified will be generated automatically.