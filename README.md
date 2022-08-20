# CoinTract
> _What is the project?_ - The CoinTract project is a project aiming to build a crypto currency app that tracks data in real time and is accessible on a mobile device. It will allow users to keep tracks of crypto assets, markets, exchanges and news. 

> _What is the MVP ?_ - The minimal viable product is a crypto currency app that perform a network call using retrofit, parsing JSON data and showing results to user

> _What are the sprinkles?_ - The sprinkles involves styling the app, adding animations and showing data in a statistically organized Candlestick Chart.

> Live demo [_Cointract_](https://appetize.io/app/qq4ovtully5s3wa43aoh23a4k4?device=pixel4xl&osVersion=11.0&scale=50). 

## Table of Contents
* [General Info](#general-information)
* [Built with](#built-with)
* [Features](#features)
* [Screenshots](#screenshots)
* [Room for Improvement](#room-for-improvement)
* [Workflow](#workflow)
* [Acknowledgements](#acknowledgements)
* [What I learned](#what-i-learned)
* [Contact](#contact)

## General Information
- The aim of this project is to provide info to the user. From crypto asset to exchange details.
- Also the purpose of this project is to allow users get info on crypto asset, exchange , markets in real time.

## Built with
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - A flow is an asynchronous version of a Sequence, a type of collection whose values are lazily produced.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 
  - [Jetpack Navigation](https://developer.android.com/guide/navigation) - Navigation refers to the interactions that allow users to navigate across, into, and back out from the different pieces of content within your app
  - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Jetpack DataStore is a data storage solution that allows you to store key-value pairs or typed objects with protocol buffers. DataStore uses Kotlin coroutines and Flow to store data asynchronously, consistently, and transactionally.
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.
- [Koin](https://insert-koin.io/) - A pragmatic and lightweight dependency injection framework for Kotlin developers.
- [Picasso](https://square.github.io/picasso/) - A powerful image downloading and caching library for Android 
- [Retofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.

## Features
- Biometric Authentication
- Light/Dark mode toggle
- Profile and Display name set Up
- Connecting to API (CoinCap and CoinStats)
- Display data in to user in a recycler view using Adapter
- Statistically show asset data on a Candlestick Chart

## Screenshots
![cointract](https://user-images.githubusercontent.com/54189037/185761622-118bb13c-15ad-461d-adb8-5f04283ac7b2.jpg)

## Room for Improvement
- Show chart data over differing time period
- Crypto conversion and price alert
- Provide Functionality for Portfolio Creation
- Search Functionality
- Adding Landscape UI and UX
- Language and Notifications set up

## Workflow
![cointract](https://user-images.githubusercontent.com/54189037/185761737-584fe92c-bfe3-4181-b63e-56f8071ebe97.png)

## Acknowledgements
- This project UI design was inspired by [MengCrypto](https://dribbble.com/faizamubarak)
- Candlestick Chart Library by [PhilJay](https://github.com/PhilJay)
- Runtime Permission [Dexter](https://github.com/nambicompany)
- Circular ImageView by [hdodenhof](https://github.com/hdodenhof)
- Mock Up design from [Freepik](https://www.freepik.com/)
- Circular Indicator [zhpanvip](https://github.com/zhpanvip/viewpagerindicator)
- Animations [Lottie](https://lottiefiles.com//)

## What i learned

There were many things that I got in touch for the first time and also becoming familiar with already known concept. Like:

- Biometric Authentication
- Coroutines, LiveData, ViewModel and Lifecycle
- Picasso and Retrofit

## Contact
Created by [Okolo](https://twitter.com/Okolo_Arthur) - feel free to contact me!


