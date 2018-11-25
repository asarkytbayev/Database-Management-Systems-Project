____entity table____
Product table - 1  // if never stock some product (delete it), then its corresponding inventory tuple will also be deleted
name                        description                         SKU(primary key)  
toothpaste             gel used to clean teeth                    AB-123456-0N
soap                  substance used to wash body                 DF-128772-GT
toothbrush        a small brush used for cleaning teeth           VB-345243-5H
candy                      everyday snack                         SX-234121-NI
hotpot           electrical equipment used to boil water          LO-812235-U9
chili jam                jam made from chili                      RX-923806-K9


InventoryRecord - 2    // (one to many)
number of units available            price/unit                                productSKU(primary key, foreign key, ON DELETE CASCADE)
120 /* need to parse to int */         4.33 /* need to parse to decimal */      AB-123456-0N /* toothpaste */
220                                    2.09                                     DF-128772-GT /* soap */
344                                    2.11                                     VB-345243-5H /* toothbrush */ 
343                                    1.22                                     SX-234121-NI /* candy */ 
231                                    123.90                                   LO-812235-U9 /* hotpot */
198                                    4.76                                     RX-923806-K9 /* chili jam */

Customer - 3
id(primary key)       name               address                       city         state       country      zipCode
1                     Aza      6024 Silver Creek Valley Road         San Jose     California       US         95116
2                     Yitong         342 Hostetter Road               Denver       Colorado        US         34901
3                     Anna         8340 Winfred Avenue                Munich       Bavaria      Germany       80336    

____relation table____
TheOrder - 4           // one customer can have many orders (one to many)  
id(primary key)      customerID(foreign key, ON DELETE CASCADE)      orderDate                                  shipmentDate
1                     1 /* Aza */                                   2018-11-23 /* need to parse to Date */       2018-11-26 /* need to parse to Date */
2                     2 /* Yitong */                                2018-11-26                                   2018-12-02
3                     1 /* Aza */                                   2018-11-27                                   2018-12-04
4                     3 /* Anna */                                  2018-12-12                                   2018-12-14

OrderRecord - 5            // if order is canceled by customer 
orderId(primary key, foreign key, ON DELETE CASCADE)     productSKU(primary key, foreign key, ON DELETE CASCADE)           number of units                      inventoryPrice(foreign key, ON DELETE CASCADE)    
1 /* Aza */                                              AB-123456-0N /* toothpaste */                                       53 /* must parse to int */              4.33 /* must parse to decimal */
1 /* Aza */                                              DF-128772-GT /* soap */                                             22                                      2.09
2 /* Yitong */                                           VB-345243-5H /* toothbrush */                                       37                                      2.11
2 /* Yitong */                                           DF-128772-GT /* soap */                                             4                                       2.09
3 /* Aza */                                              SX-234121-NI /* candy */                                            12                                      1.22
3 /* Aza */                                              LO-812235-U9 /* hotpot */                                           10                                      123.90
3 /* Aza */                                              RX-923806-K9 /* chili jam */                                        60                                      4.76
4 /* Anna */                                             SX-234121-NI /* candy */                                            50                                      1.22
4 /* Anna */                                             LO-812235-U9 /* hotpot */                                           1                                       123.90

// delete an order (4) in TheOrder -> orderId on delete cascade in OrderRecord -> number in InventoryRecord reverts


1  1     toothpaste      AB-123456-0N        4.33      Aza     6024 Silver Creek Valley Road       San Jose      California      US       95116       2018-11-23      2018-11-26      53     
1  1     soap            DF-128772-GT        2.09      Aza     6024 Silver Creek Valley Road       San Jose      California      US       95116       2018-11-23      2018-11-26      22
2  2     toothbrush      VB-345243-5H        2.11      Yitong  342 Hostetter Road                  Denver        Colorado        US       34901       2018-11-26      2018-12-02      37
2  2     soap            DF-128772-GT        2.09      Yitong  342 Hostetter Road                  Denver        Colorado        US       34901       2018-11-26      2018-12-02      4
3  1     candy           SX-234121-NI        1.22      Aza     6024 Silver Creek Valley Road       San Jose      California      US       95116       2018-11-27      2018-12-04      12
3  1     hotpot          LO-812235-U9        123.90    Aza     6024 Silver Creek Valley Road       San Jose      California      US       95116       2018-11-27      2018-12-04      10
3  1     chili jam       RX-923806-K9        4.76      Aza     6024 Silver Creek Valley Road       San Jose      California      US       95116       2018-11-27      2018-12-04      60
4  3     candy           SX-234121-NI        1.22      Anna    8340 Winfred Avenue                 Munich        Bavaria         Germany  80336       2018-12-12      2018-12-14      50
4  3     hotpot          LO-812235-U9        123.90    Anna    8340 Winfred Avenue                 Munich        Bavaria         Germany  80336       2018-12-12      2018-12-14      1
 
