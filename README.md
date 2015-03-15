## Introduction

Sometimes we just don't need a large, complicated, over engineered, expensive facility to do the small things that are needed to develop a system.  For those time there is *smallstuff*, an effort consisting of small projects that look to develop web services, and other small things, to address well defined functional areas.  The current vision for *smallstuff* is to address ETL and workflow capabilities. 

## Projects

The *smallstuff* effort is currently made up of the following projects:
* smallstuff-etl-common - classes and resources used by *-etl-* projcts
* smallstuff-etl-fileuploader - web service to support fileupload action and wrapper for processing (e.g., staging/loading data to a DB) the file
* smallstuff-etl-filewatcher - servlet context listeners that detect files landing in a set of directories; supporting web services for stopping, restarting, and reporting the status of listeners; and then wrappers for filtering (e.g., handling trigger files) and processing files (e.g., staging/loading data to a DB)  

## License

All of the *smallstuff* is licensed under the Apache version 2.0 license 

