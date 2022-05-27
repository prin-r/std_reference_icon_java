# Java Std Reference

Build
```shell
./gradlew optimizedJar
```

Deployment
```shell
./gradlew std-reference-basic:deployToLisbon -PkeystoreName=keystore.json  -PkeystorePass=PASSWORD
./gradlew std-reference-proxy:deployToLisbon -PkeystoreName=keystore.json  -PkeystorePass=PASSWORD

./gradlew std-reference-basic:deployToBerlin -PkeystoreName=keystore.json  -PkeystorePass=PASSWORD
./gradlew std-reference-proxy:deployToBerlin -PkeystoreName=keystore.json  -PkeystorePass=PASSWORD
```

Relay
```shell
# lisbon
../goloop/bin/goloop rpc sendtx call --to cxba8f1078a0dd1fa44c03fd9d1deb817a67132377 --method relay --param _symbols=BTC,ETH --param _rates=36020280000000,2701019900000 --param resolveTime=1651834443 --param requestID=8001584 --uri https://lisbon.net.solidwallet.io/api/v3 --key_store keystore.json --key_password PASSWORD --nid 2 --step_limit=1000000

# Berlin
../goloop/bin/goloop rpc sendtx call --to cx1abe7fb60f2721c6ff2a76676f6b6c5ddebe1218 --method relay --param _symbols=BTC,ETH --param _rates=36020280000000,2701019900000 --param resolveTime=1651834443 --param requestID=8001584 --uri https://berlin.net.solidwallet.io/api/v3 --key_store keystore.json --key_password PASSWORD --nid 7 --step_limit=1000000
```

### Deployed contracts on Lisbon

- [std-reference-basic](https://lisbon.tracker.solidwallet.io/contract/cxba8f1078a0dd1fa44c03fd9d1deb817a67132377)
- [std-reference-proxy](https://lisbon.tracker.solidwallet.io/contract/cx898369ed6dc203beb63e970b12f6acc6d058eac8)
- [example relayed tx](https://lisbon.tracker.solidwallet.io/transaction/0x8b253be4541ccb02c1b2903a6b19bea47edb80bc0b8ac54b47b2358cf6a795e2)

example command for price querying from std-reference-proxy
```shell
../goloop/bin/goloop rpc call --to cx898369ed6dc203beb63e970b12f6acc6d058eac8 --method _getReferenceDataBulk --param _bases="BTC,ETH,USD,BTC,ETH" --param _quotes="USD,USD,USD,ETH,ETH" --uri https://lisbon.net.solidwallet.io/api/v3

[
  [
    "0x7a0a9cd78609b6c0000",
    "0x5de55ad33e8c0",
    "0x5dfb9cd61073b"
  ],
  [
    "0x926c2e586ee91bc000",
    "0x5de55ad33e8c0",
    "0x5dfb9cd61073b"
  ],
  [
    "0xde0b6b3a7640000",
    "0x5dfb9cd61073b",
    "0x5dfb9cd61073b"
  ],
  [
    "0xb9124dc7bbd25324",
    "0x5de55ad33e8c0",
    "0x5de55ad33e8c0"
  ],
  [
    "0xde0b6b3a7640000",
    "0x5de55ad33e8c0",
    "0x5de55ad33e8c0"
  ]
]
```

### Deployed contracts on Berlin

- [std-reference-basic](https://berlin.tracker.solidwallet.io/contract/cx1abe7fb60f2721c6ff2a76676f6b6c5ddebe1218)
- [std-reference-proxy](https://berlin.tracker.solidwallet.io/contract/cxd9f4ee69b15ae0fbb8cd95b183f420ca54ef0e5c)
- [example relayed tx](https://berlin.tracker.solidwallet.io/transaction/0x391be4d153afcf51b32fb0f68d2c275d5578bc3d54194749d19848ddea75ec3c)

example command for price querying from std-reference-proxy
```
../goloop/bin/goloop rpc call --to cxd9f4ee69b15ae0fbb8cd95b183f420ca54ef0e5c --method _getReferenceDataBulk --param _bases="BTC,ETH,USD,BTC,ETH" --param _quotes="USD,USD,USD,ETH,ETH" --uri https://berlin.net.solidwallet.io/api/v3

[
  [
    "0x7a0a9cd78609b6c0000",
    "0x5de55ad33e8c0",
    "0x5dfb9c6f099cb"
  ],
  [
    "0x926c2e586ee91bc000",
    "0x5de55ad33e8c0",
    "0x5dfb9c6f099cb"
  ],
  [
    "0xde0b6b3a7640000",
    "0x5dfb9c6f099cb",
    "0x5dfb9c6f099cb"
  ],
  [
    "0xb9124dc7bbd25324",
    "0x5de55ad33e8c0",
    "0x5de55ad33e8c0"
  ],
  [
    "0xde0b6b3a7640000",
    "0x5de55ad33e8c0",
    "0x5de55ad33e8c0"
  ]
]
```
