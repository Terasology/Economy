# Economy

This module aims to provide a very generic framework to simulate an economy with consumers and producers. On top of that however, it provides a robust method to share/trade/exchange resources between entities. It does so under a very libertarian credo: Use any storage component you want.

To see how you can get started with it and how you can hook it into your module see the [wiki](https://github.com/Terasology/Economy/wiki)! 

Credits:
* Gold coin: https://openclipart.org/detail/227733/pixel-gold-coin


## Purpose: 
### What is the module for?
This module aims to provide a very generic framework to simulate an economy with consumers and producers. On top of that however, it provides a robust method to share/trade/exchange resources between entities. It does so under a very libertarian credo: Use any storage component you want. It now also contains a type of currency for player to use while facilitating the trade

### Why would players be interested in it?
The addition of an Economy brings opens a lot of doors in many other cities modules (currently used in Dynamic Cities to stimulate the economy and world building). The gameplay additions could include: 
* Looters: Players who would hide and scavenge any other player for coins or resources. 
* Traders: Players who would go between cities to interact with different NPCs to trade (soon to be added)
* And MANY MORE!

## Structure: 
### What does the module contain?
The module is currently divided into two sections, The currency and the background economy.
* The currency is a simple gold coin with no asserted value which could be used for trade.
* The economical system has multiple sub sections:
  1.	Producing resources: In this the Market Subscriber Component could be used to specify what resources would be required    to produce the specific product or resource.
  2.	Production and consumption: There is a need to specify the time in between the production and consumption events to simulate an economy (due to the low amount of players) this creates fake but yet controllable supply and demand in the economy
  3.	Creating, storing, withdrawing and destroying resources: In the events folder, you’ll find different actions which can be used to control the availability of your product (entity)

## Extensibility: 
### How can the module be extended by other developers?
Currently the module is used to extend (run in) the Dynamic cities. 
* The possible addition of NPC trading option via a menu (NUI) system and an Auction house/Trading centre are highly recommended. 
* In addition to that, a more comprehensive currency issuing system and a more detailed creation and destruction of resources (self adjusted for demand) could be another extension.
