# Economy

This module aims to provide a very generic framework to simulate an economy with consumers and producers. On top of that however, it provides a robust method to share/trade/exchange resources between entities. It does so under a very libertarian credo: Use any storage component you want.

To see how you can get started with it and how you can hook it into your module see the [wiki](https://github.com/Terasology/Economy/wiki)!

## Features

### Optional Shop Screen

This module can provide an alternative _Inventory Screen_ that displays an in-game shop with purchasable items in 
addition to the regular player inventory. Whether this alternative screen is shown depends on the presence (or absence) 
of the `AllowShopScreenComponent` on a client entity.

**By default, clients cannot see the alternative shop screen.**

To enable it, add an event handler to ensure that joining clients will have the `AllowShopScreenComponent` attached to
their entity. You may use the following code snippet and add it to a registered system.

```java
/**
 * Ensure that the client has the {@link AllowShopScreenComponent} such that they can use the in-game shop from the Economy module.
 * @param event connection event when a client (and their player) joins a game/server
 * @param entity the client entity
 */
@ReceiveEvent
public void onConnect(ConnectedEvent event, EntityRef entity) {
    entity.upsertComponent(AllowShopScreenComponent.class, c -> c.orElse(new AllowShopScreenComponent()));
}
```

## Credits

Gold coin: https://openclipart.org/detail/227733/pixel-gold-coin
