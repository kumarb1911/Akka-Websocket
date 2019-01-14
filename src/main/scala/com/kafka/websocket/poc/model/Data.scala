package com.kafka.websocket.poc.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
/**
  * Created by suresh on 13-01-2019.
  */

final case class HomeLoan(category:String,rateOfInterest:String,maxTenure:String,eliibilityCriteria:List[String],processingFee:String)
final case class CarLoan(category:String,rateOfInterest:String,maxTenure:String,eliibilityCriteria:List[String],processingFee:String)
final case class CreditCard(category:String,eliibilityCriteria:List[String],processingFee:String,documentsRequired:List[String],validProofs:List[String])
final case class PropertyLoan(category:String,rateOfInterest:String,maxTenure:String,eliibilityCriteria:List[String],processingFee:String)
final case class CommercialLoan(category:String,rateOfInterest:String,maxTenure:String,eliibilityCriteria:List[String],processingFee:String)


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val HomeLoanFormats = jsonFormat5(HomeLoan)
  implicit val CarLoanFormats = jsonFormat5(CarLoan)
  implicit val CreditCardFormats = jsonFormat5(CreditCard)
  implicit val PropertyLoanFormats = jsonFormat5(PropertyLoan)
  implicit val CommercialLoanFormats = jsonFormat5(CommercialLoan)





}

object Data {
  implicit val HomeLoanJsonWriter = new JsonWriter[HomeLoan] {
    def write(hl: HomeLoan): JsValue = {
      JsObject(
        "category" -> JsString(hl.category),
        "Rate Of Interest" -> JsString(hl.rateOfInterest),
        "Max Loan Tenure" -> JsString(hl.maxTenure),
        "Eligibility Criteria"->JsArray(hl.eliibilityCriteria.map(s=>JsString(s)).toVector),
        "Processing Fee"->JsString(hl.processingFee)
      )
    }
  }

  implicit val CarLoanJsonWriter = new JsonWriter[CarLoan] {
    def write(cl: CarLoan): JsValue = {
      JsObject(
        "category" -> JsString(cl.category),
        "Rate Of Interest" -> JsString(cl.rateOfInterest),
        "Max Loan Tenure" -> JsString(cl.maxTenure),
        "Eligibility Criteria"->JsArray(cl.eliibilityCriteria.map(s=>JsString(s)).toVector),
        "Processing Fee"->JsString(cl.processingFee)
      )
    }
  }

  implicit val PropertyLoanJsonWriter = new JsonWriter[PropertyLoan] {
    def write(pl: PropertyLoan): JsValue = {
      JsObject(
        "category" -> JsString(pl.category),
        "Rate Of Interest" -> JsString(pl.rateOfInterest),
        "Max Loan Tenure" -> JsString(pl.maxTenure),
        "Eligibility Criteria"->JsArray(pl.eliibilityCriteria.map(s=>JsString(s)).toVector),
        "Processing Fee"->JsString(pl.processingFee)
      )
    }
  }

  implicit val CommercialLoanJsonWriter = new JsonWriter[CommercialLoan] {
    def write(ml: CommercialLoan): JsValue = {
      JsObject(
        "category" -> JsString(ml.category),
        "Rate Of Interest" -> JsString(ml.rateOfInterest),
        "Max Loan Tenure" -> JsString(ml.maxTenure),
        "Eligibility Criteria"->JsArray(ml.eliibilityCriteria.map(s=>JsString(s)).toVector),
        "Processing Fee"->JsString(ml.processingFee)
      )
    }
  }

  implicit val CreditCardJsonWriter = new JsonWriter[CreditCard] {
    def write(cc: CreditCard): JsValue = {
      JsObject(
        "category" -> JsString(cc.category),
        "Processing Fee" -> JsString(cc.processingFee),
        "Documentation" -> JsArray(cc.documentsRequired.map(s=>JsString(s)).toVector),
        "Eligibility Criteria"->JsArray(cc.eliibilityCriteria.map(s=>JsString(s)).toVector),
        "Valid Proofs"->JsArray(cc.validProofs.map(s=>JsString(s)).toVector)
      )
    }
  }

  val sampleData = Map(
    "homeloan" -> HomeLoan("Home Loan","9.5%","30 years",
      List("The applicant should be either a salaried person or a self-employed with the regular income earnings.",
  "A salaried applicant must have at least two years of work experience.",
  "Maximum age limit is 70 years at the time of maturity of the loan."),"3% of the loan amount").toJson.toString(),
    "carloan" -> CarLoan("Car Loan","10.2%","7 years",
      List("Minimum 21 years of age.",
  "Maximum 70 years of age at maturity (conditions apply)",
  "Minimum Net Annual Salary of Rs. 2,40,000 p.a. for all approved car models.",
  "Income eligibility based on latest salary slip and Form 16.",
  "Minimum of 1 year continuous employment."),"2% of the loan amount").toJson.toString(),
    "propertyloan" -> PropertyLoan("Property Loan","13.5%","25 years",
      List("Primary applicant age should not be more than 50 years for salaried and 6o years for self employed",
        "Property should have registered on applicants name",
        "Property should be in India"),"6% of the loan amount").toJson.toString(),
  "commercialloan" -> CommercialLoan("Commercial Loan","12%","15 years",
    List("The applicant should be min 21 years & max. 65 years.",
      "150,000 p.a. should be the minimum annual income.",
      "4 million should be the minimum Turnover.",
      "Years in business should be minimum of 3 years in current firm and 5 years of overall experience.",
      "At least for the last 2 years the business must be profit making ",
      "Co-applicants: This is optional to the applicant, not mandatory in case of business loan."),
    "5% of the loan amount").toJson.toString(),
    "creditcard" -> CreditCard("Credit Card",List("Applicant should be at least 18 years old",
      "Applicant should be salaried or self-employed, with a regular source of income to pay back the credit card bills",
      "He/She should have a savings account in his/her name",
      "He/she should not have a bad credit history"),"Nill",
      List("Address Proof","Tax Returns","Pay Slips"),
      List("Lease Agreement","Form 16","HR Letters")).toJson.toString()
  )
}