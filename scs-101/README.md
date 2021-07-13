
### This Project has 5 subprojects:

## scs-101-commons
[![scs-101 commons ci](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101commons.yml/badge.svg?branch=main)](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101commons.yml)

lib project for core projects

## 1- scs-101-inventory-check

[![scs-101 inventory-check ci](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101inventorycheck.yml/badge.svg?branch=main)](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101inventorycheck.yml)


## 2- scs-101-order

[![scs-101 order ci](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101order.yml/badge.svg?branch=main)](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101order.yml)

## 3- scs-101-order-branch

[![scs-101 order-branch ci](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101orderbranch.yml/badge.svg?branch=main)](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101orderbranch.yml)

## 4- scs-101-shipped

[![scs-101 shipped ci](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101shipped.yml/badge.svg?branch=main)](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101shipped.yml)

## 5- scs-101-shipping

[![scs-101 shipping ci](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101shipping.yml/badge.svg?branch=main)](https://github.com/ehsaniara/scs-kafka-intro/actions/workflows/scs101shipping.yml)

# Quick Start

```shell
ORDER_UUID=$(curl --silent -H 'Content-Type: application/json' -d "{\"itemName\":\"book\"}" http://localhost:8080/order | jq -r '.orderUuid') && for i in `seq 1 15`; do sleep 1; echo $(curl --silent "http://localhost:8080/order/status/"$ORDER_UUID); done;
```
