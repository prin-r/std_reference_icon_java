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
../goloop/bin/goloop rpc sendtx call --to cxfd4e337b69adaa64fe2631d5b7b0d133a8534107 --method relay --param _symbols=BTC,ETH --param _rates=36020280000000,2701019900000 --param resolveTime=1651834443 --param requestID=8001584 --uri https://lisbon.net.solidwallet.io/api/v3 --key_store keystore.json --key_password PASSWORD --nid 2 --step_limit=1000000

# Berlin
../goloop/bin/goloop rpc sendtx call --to cx41d9967fb59a72952dea393628475932b4c2ae02 --method relay --param _symbols=BTC,ETH --param _rates=36020280000000,2701019900000 --param resolveTime=1651834443 --param requestID=8001584 --uri https://berlin.net.solidwallet.io/api/v3 --key_store keystore.json --key_password PASSWORD --nid 7 --step_limit=1000000
```

### Deployed contracts on Lisbon

- [std-reference-basic](https://lisbon.tracker.solidwallet.io/contract/cxfd4e337b69adaa64fe2631d5b7b0d133a8534107)
- [std-reference-proxy](https://lisbon.tracker.solidwallet.io/contract/cx03018fc77ee8862c25f1656161f8a6dbe53620d3)
- [example relayed tx](https://lisbon.tracker.solidwallet.io/transaction/0xcd7d49323b8c29bab974e3c62ddab9ef2349a52533cf6c6fe1707943760fad9c)

example command for price querying from std-reference-proxy
```shell
../goloop/bin/goloop rpc call --to cx03018fc77ee8862c25f1656161f8a6dbe53620d3 --method _get_reference_data_bulk --param _bases="BTC,ETH,USD,BTC,ETH" --param _quotes="USD,USD,USD,ETH,ETH" --uri https://lisbon.net.solidwallet.io/api/v3
[
  {
    "last_update_base": "0x5de55ad33e8c0",
    "last_update_quote": "0x5e1c9cb893a19",
    "rate": "0x7a0a9cd78609b6c0000"
  },
  {
    "last_update_base": "0x5de55ad33e8c0",
    "last_update_quote": "0x5e1c9cb893a19",
    "rate": "0x926c2e586ee91bc000"
  },
  {
    "last_update_base": "0x5e1c9cb893a19",
    "last_update_quote": "0x5e1c9cb893a19",
    "rate": "0xde0b6b3a7640000"
  },
  {
    "last_update_base": "0x5de55ad33e8c0",
    "last_update_quote": "0x5de55ad33e8c0",
    "rate": "0xb9124dc7bbd25324"
  },
  {
    "last_update_base": "0x5de55ad33e8c0",
    "last_update_quote": "0x5de55ad33e8c0",
    "rate": "0xde0b6b3a7640000"
  }
]
```

### Deployed contracts on Berlin

- [std-reference-basic](https://berlin.tracker.solidwallet.io/contract/cx41d9967fb59a72952dea393628475932b4c2ae02)
- [std-reference-proxy](https://berlin.tracker.solidwallet.io/contract/cx7906b65f91980eb4d6541d9cab3550123c1a7cb1)
- [example relayed tx](https://berlin.tracker.solidwallet.io/transaction/0xe3a80e5b62fbd00bb01c0e71f7d93a20d4b20e76f9eed8a38429fecfed425be0)

example command for price querying from std-reference-proxy
```
../goloop/bin/goloop rpc call --to cx41d9967fb59a72952dea393628475932b4c2ae02 --method _get_reference_data_bulk --param _bases="BTC,ETH,USD,BTC,ETH" --param _quotes="USD,USD,USD,ETH,ETH" --uri https://berlin.net.solidwallet.io/api/v3
[
  {
    "last_update_base": "0x5de55ad33e8c0",
    "last_update_quote": "0x5e1c995d90176",
    "rate": "0x7a0a9cd78609b6c0000"
  },
  {
    "last_update_base": "0x5de55ad33e8c0",
    "last_update_quote": "0x5e1c995d90176",
    "rate": "0x926c2e586ee91bc000"
  },
  {
    "last_update_base": "0x5e1c995d90176",
    "last_update_quote": "0x5e1c995d90176",
    "rate": "0xde0b6b3a7640000"
  },
  {
    "last_update_base": "0x5de55ad33e8c0",
    "last_update_quote": "0x5de55ad33e8c0",
    "rate": "0xb9124dc7bbd25324"
  },
  {
    "last_update_base": "0x5de55ad33e8c0",
    "last_update_quote": "0x5de55ad33e8c0",
    "rate": "0xde0b6b3a7640000"
  }
]
```
