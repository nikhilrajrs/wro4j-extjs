# wro4j-extjs
Java utility to automatically create wro4j configuration for extjs based applications.

Utility starts with the app.js file and recursively visits each of the files that make up the app and prints out the wro4j configuration for the same ensuring that the load order is maintained. It considers both the 'requires:' as well as 'model:' tags to find out the dependencies.

The output can and should be directly pasted into the wro.xml.

Please change the basePath, intermediary and applicationNamePrefix variables according to your environment.
