FastInfoset (FI) Samples

The samples project is in its early stage and not complete. More samples may be added in the near future.
We welcome feedbacks and suggestions on use cases. Please write to users@fi.dev.java.net.

Listed here are samples that are planned and completed:


1. FI SAX parser (to be completed)
    Demonstrate the use of FI SAX parser to transform a FI document into XML file. 

2. FI SAX serializer
    Demonstrate the use of FI SAX serializer to transform a XML file into FI document. The sample
takes data/inv1.xml and converts it into data/inv1.finf
    To run the sample, go to the samples directory and type:
    ant FISAXSerializer

3. FI StAX serializer
    Demonstrate the use of FI StAX serializer to transform a XML file into FI document. The sample
takes data/inv1.xml and converts it into data/inv1.finf
    To run the sample, go to the samples directory and type:
    ant FIStAXSerializer

3. FI StAX EventReader
    Demonstrate the use of FI StAX EventReader to read FI and XML files. The sample
reads data/inv1a.xml and data/inv1a.finf and displays event types as it goes through
the documents.
    To run the sample, go to the samples directory and type:
    ant EventReader

3. FI StAX StreamReader
    Demonstrate the use of FI StAX StreamReader to read FI files. The sample reads 
data/inv1.finf and displays contents as it goes through the document.
    To run the sample, go to the samples directory and type:
    ant StreamReader