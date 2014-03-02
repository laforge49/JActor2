@echo off

echo "Adding changes"

git add -A

echo "Commiting changes"

git commit -m "Newest results"

echo "Pulling, just in case"

git pull origin gh-pages

echo "Pushing changes"

git push origin gh-pages

echo "Done, if you didn't get an error. :D"
