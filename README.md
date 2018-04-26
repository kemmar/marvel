# Marvel Application (Akka-Http)

## Install Latest SBT
http://www.scala-sbt.org/1.0/docs/Setup.html

## Run Application

In command line in folder path type 

```
sbt clean run \
-Dmarvel.url="https://gateway.marvel.com" \
-Dmarvel.privateKey="{PK}" \
-Dmarvel.publicKey="{pubK}" \
-Dmarvel.apiKey="{apiKey}" \
-Dgoogle.apiKey= "{apiKey}" \
-Dgoogle.url="https://translation.googleapis.com"

```

### swagger

http://localhost:8080/swagger
 
## preRequisites
  
JDK: Java(8) preferable

SBT: 13.*

scala: 2.12.*

## GIT

https://github.com/kemmar/marvel


# Usefull bit of code
```
import scala.annotation.tailrec

case class TotalCosts(
                       stampDuty: BigDecimal,
                       deposit: BigDecimal,
                       rentalYield: Option[BigDecimal] = None
                     )


def houseCostCalculator(cost: BigDecimal, firstTimeBuyer: Boolean, buyToLet: Boolean = false, potentialRent: Option[BigDecimal] = None): TotalCosts = {
  val stampDuty = calculateStampDuty(cost, firstTimeBuyer)
  val deposit = calculateDeposit(cost, firstTimeBuyer, buyToLet)

  val `yield` = potentialRent.map { rent =>
    rent * 12 / cost
  }

  TotalCosts(stampDuty, deposit, `yield`)
}


def calculateStampDuty(cost: BigDecimal, firstTimeBuyer: Boolean): BigDecimal = {

  type Think = (BigDecimal, BigDecimal)

  val OverOnePointFiveMill: Think = (1500000, 0.12)
  val OverNineTwoFiveThou: Think = (925000, 0.10)
  val OverTwoFifty: Think = (250000, 0.05)
  val OverOneTwoFiveThou: Think = (125000, 0.02)
  val Base: Think = (0, 0.00)

  def findRate(rate: BigDecimal): BigDecimal = if (firstTimeBuyer) rate else rate + 0.03

  def segmentations(currentCost: BigDecimal, sc: List[BigDecimal] = Nil): List[BigDecimal] = {

    def calc(c: BigDecimal, value: Think): List[BigDecimal] = {
      val difference = c - value._1

      val costRate = findRate(value._2)
      
      
        if (value == Base)
          difference * costRate :: sc
        else
          segmentations(value._1, difference * costRate :: sc)
    }
    
    currentCost match {
      case _ if currentCost > OverOnePointFiveMill._1 => calc(currentCost, OverOnePointFiveMill)

      case _ if currentCost > OverNineTwoFiveThou._1 => calc(currentCost, OverNineTwoFiveThou)

      case _ if currentCost > OverTwoFifty._1 => calc(currentCost, OverTwoFifty)

      case _ if currentCost > OverOneTwoFiveThou._1 => calc(currentCost, OverOneTwoFiveThou)

      case _ => calc(currentCost, Base)
    }
  }

  val t = segmentations(cost)

  t.sum
}

def calculateDeposit(cost: BigDecimal, firstTimeBuyer: Boolean, buyToLet: Boolean): BigDecimal = {
  val rate: BigDecimal = if (firstTimeBuyer) 0.05
  else if(buyToLet) 0.25
  else 0.1

  cost * rate
}


houseCostCalculator(400000, false, false, Some(1500))

```
