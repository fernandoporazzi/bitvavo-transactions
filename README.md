# bitvavo-transactions

A little aggregator that outputs the amount of each transaction type per asset.

## Why?

Just because!

## Running

Download the CSV report from your Bitvavo account.
Place it somewhere.

```shell
# if .jar and .csv are not in the same folder, the full path to the csv is required
$ java -jar bitvavo-transactions.jar --path /path/to/bitvavo_history.csv

# if .jar and .csv are in the same folder:
$ java -jar bitvavo-transactions.jar --path bitvavo_history.csv
```

## Output example:

```
- BTC:
    Amount of transactions: 69
    Purchased 0.21 BTC across 151 transactions
    Deposited 0.005 BTC across 4 transactions
    Staked 0,00041811 BTC across 10 transactions
    Withdrew -0,001 BTC in 8 transactions
    Final balance: 0,26195614 BTC
    Total BTC transacted (including buy and sell orders, deposits, staking and withdrawals): 0.12 BTC

- SOL:
    Amount of transactions: 10
    Purchased 1.41630872 SOL across 1 transactions
    Deposited 0.0 SOL across 0 transactions
    Staked 0,02736213 SOL across 13 transactions
    Withdrew 0,00000000 SOL in 1 transactions
    Final balance: 0,00275836 SOL
    Total SOL transacted (including buy and sell orders, deposits, staking and withdrawals): 2.8873417 SOL

```