package com.egzonberisha.weatherappandroid.Model;

public class Sys {
 private double message;
 private String country;
 private int sunrise;
 private int sunset;


 // Getter Methods

 public double getMessage() {
  return message;
 }

 public String getCountry() {
  return country;
 }

 public int getSunrise() {
  return sunrise;
 }

 public int getSunset() {
  return sunset;
 }

 // Setter Methods

 public void setMessage(float message) {
  this.message = message;
 }

 public void setCountry(String country) {
  this.country = country;
 }

 public void setSunrise(int sunrise) {
  this.sunrise = sunrise;
 }

 public void setSunset(int sunset) {
  this.sunset = sunset;
 }
}
