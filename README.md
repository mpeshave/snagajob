SnagaJob - Application Evaluation.
----------------------------

Application to evalute incoming applications from protential candidates. Use the jar-with-dependencies to run the
application.

1) Driver - Starts a file wathcer on incoming application dir and starts the evaluation enginer.
To run:
java -cp snagajob-1.0-SNAPSHOT-jar-with-dependencies.jar org.snagajob.Driver <incoming_app_dir> <questionnaire_dir> <output_dir> <unprocessed_dir>

incoming_app_dir - dir where new applcaition doc will be dropped.
questionnaire_dir - dir that hold question json files.
output_dir - dir where to store results
unprocessd_dir - incase an application cant be processed it will be move to this dir.

The resource folder hold sample application files under "applications" and associated "questionnaires".
All the application can either be push in the incoming dir up front or as and when needed after the Driver has
been started.

2) DisplayResults - Displays results for a certain questionnaire for a defined time window.
To run:
java -cp snagajob-1.0-SNAPSHOT-jar-with-dependencies.jar org.snagajob.DisplayResults <output_dir> <questionnaire_name> <from_date> [to_date]

output_date - same as used while running dirver.
questionnaire_name - must match the file name or one specified in applications.
from_date - date: YYYY-MM-DD

