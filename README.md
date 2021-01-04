# Purpose of application

Urban explorer is an app which serves the knowledge about interesting places
in the user's neighborhood.

It fetches right now from two places - wiki API and panoramio API
(which shows to have many downtimes). The another third is planned to be
introduced due to low SLA level of panoramio.

For changes introduces in the app versions look for @see changelog.md .
For change requests, new features and bugs see the app's jira.

Currently used libraries:
- android sdk,
- crashlytics (for bug reporting),
- universal image loader
- google support libs.

# Post mortem update

By now (since Nov 2016 ), Panoramio is defunct and it was 50% of the application data sources (other was wiki) https://www.panoramio.com/ https://en.wikipedia.org/wiki/Panoramio . It was available since Nov 2016 under the google play key pl.tpolgrabia.urbanexplorer and taken down on March 2018 by Google due to inactivity (not updating information about the application).