Where's Felipe?
by Felipe Oliveira [http://twitter.com/_felipera]
December 31st 2011


== Introduction ==

Location-based application powered by Play Framework, Scala, Google Maps v3, PostgreSQL and Anorm deployed on Heroku (http://felipe.herokuapp.com/).


== Deployment ==

- heroku create -s cedar

- heroku pg:promote SHARED_DATABASE

- git init

- git add .

- git commit -a -m "Initial Commit"

- git remote add heroku git@heroku.com:#REPLACE_APP_NAME_HERE#.git

- git push heroku master; heroku run "play evolutions:apply --%prod"; heroku open
