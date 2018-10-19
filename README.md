# Mobile assignment PS Readme #

## Configuring the App ##

This app is hardcoded with working API Keys. However, if you wish to use
your own, you may choose to configure this app's gradle.properties file
fill it with your OpenWeather API key after uncommenting the lines out.

## Feedback ##

It is highly recommended to use third party libraries, especially with
limited time constraints. This app's concept is simple sure, but the
development could have produced a much more superior product if outside
libraries were permitted or if the time constraint was not limited to
four days. Unit Testing requirement in android is extremely difficult as
well, a colleague has written a blog about this, if you are interested,
visit these links:

* https://www.philosophicalhacker.com/2015/04/17/why-android-unit-testing-is-so-hard-pt-1/
* https://www.philosophicalhacker.com/post/why-android-testing-is-so-hard-historical-edition/

It might also be worth mentioning, Google has even come out and bluntly
said that developers should not use unit testing at all.

Of the requirements, I have met the following:

* Default main "home" screen
    * Top is Google Maps and their API (this had to be used with my personal key)
    * Bottom is a list of bookmarked/saved locations
    * Adding/removing is just tapping on the map and swiping on the list respectively
    * Renaming bookmarked locations is possible by either long-pressing the name on the list, or tapping on the InfoWindow of the map marker
* Location details "city details" screen
    * With coordinator layout - loads data from the OpenWeather API and makes another call for the 5 day forecast at the bottom of the screen
    * It is worth noting that the data is cached per instance, and will be reloaded when switching bookmarked locations
* Settings menu
    * Accessed by the floating action button on the main screen
    * Only 3 options here: help/tutorial screen, change Unit Of Measure preference and delete all bookmarks
* Help Screen fragment has a webview with several video tutorials

## Notes ##
Thank you for taking the time to read this. Below will be my personal
repository and a copy/paste from the original requirements.
Please note: the commits prior to October 15 were from when I was sick - I took
the week off but wanted to try the assignment unofficially - this changed
on the 15th when I was accepted to take the assessment - so I began to
take this more seriously as you can see more of the bulk commits.

### Repository ###
https://github.com/JLaguardia/OpenWeatherAPI

## Instructions ##


### Overview ###

The goal of this assignment is to evaluate the problem solving skills, UX judgement and code quality of the candidate.

Weather, everybody wants to know how it is going to be during the week. Will it be rainy, windy, or sunny? Luckily for us, in the information age, there are open APIs to retrieve information about it.
For this assignment you will be using the API from: http://openweathermap.org/api. The API key is provided at the end of the statement, or you can request your own by registering on the website for free.

### Requirements ###

Your app should at least contain the following screens:

* Home screen:
	* Showing a list of locations that the user has bookmarked previously.
	* Show a way to remove locations from the list
	* Add locations by placing a pin on map.
* City screen: once the user clicks on a bookmarked city this screen will appear. On this screen the user should be able to see:
	* Today’s forecast, including: temperature, humidity, rain chances and wind information
* Help screen: The help screen should be done using a webview, and contain information of how to use the app, gestures available if any, etc.

### Bonus ###

The following bonus points can be implemented:

* Settings page: where the user can select some preferences like: unit system
(metric/imperial), any other user setting you consider relevant, e.g. reset cities
bookmarked.
* On the city screen: show the 5-days forecast, including: temperature, humidity, rain
chances and wind information.
* On the home screen, implement a list of known locations with search capabilities.

How navigation occurs, or how elements are placed on the screen is open for interpretation and creativity.

### Additional Requirements ###

Additionally, the following requirements have to be met:

* Alpha/beta versions of the IDE are forbidden, you must work with the stable version of
the IDE
* The API has to be consumed in JSON format
* The UI has to be responsive (landscape and portrait orientations, and tablet resolutions
must be supported)
* The code has to be published on GitHub or Bitbucket. We want to see the progress evolution

### Android Requirements ###

* For Android:
	* Language must be Java
	* The coordinator layout must be used at least in one of the screens.
	* UI has to be implemented using 1 activity with multiple fragments
	* Only 3rd party libraries allowed are: GSON or Jackson
	* Compatible with Android 4.1+



