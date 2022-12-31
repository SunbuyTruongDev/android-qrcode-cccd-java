package com.sunbuy.scancode.extensions

import android.content.Intent
import com.google.mlkit.vision.barcode.common.Barcode
import com.sunbuy.scancode.content.AddressParcelable
import com.sunbuy.scancode.content.CalendarDateTimeParcelable
import com.sunbuy.scancode.content.CalendarEventParcelable
import com.sunbuy.scancode.content.ContactInfoParcelable
import com.sunbuy.scancode.content.EmailParcelable
import com.sunbuy.scancode.content.GeoPointParcelable
import com.sunbuy.scancode.content.PersonNameParcelable
import com.sunbuy.scancode.content.PhoneParcelable
import com.sunbuy.scancode.content.QRContent
import com.sunbuy.scancode.content.QRContent.CalendarEvent
import com.sunbuy.scancode.content.QRContent.CalendarEvent.CalendarDateTime
import com.sunbuy.scancode.content.QRContent.ContactInfo
import com.sunbuy.scancode.content.QRContent.ContactInfo.Address
import com.sunbuy.scancode.content.QRContent.ContactInfo.Address.AddressType
import com.sunbuy.scancode.content.QRContent.ContactInfo.PersonName
import com.sunbuy.scancode.content.QRContent.Email
import com.sunbuy.scancode.content.QRContent.Email.EmailType
import com.sunbuy.scancode.content.QRContent.GeoPoint
import com.sunbuy.scancode.content.QRContent.Phone
import com.sunbuy.scancode.content.QRContent.Phone.PhoneType
import com.sunbuy.scancode.content.QRContent.Plain
import com.sunbuy.scancode.content.QRContent.Sms
import com.sunbuy.scancode.content.QRContent.Url
import com.sunbuy.scancode.content.QRContent.Wifi
import com.sunbuy.scancode.content.SmsParcelable
import com.sunbuy.scancode.content.UrlBookmarkParcelable
import com.sunbuy.scancode.content.WifiParcelable



private fun PhoneParcelable.toPhone(rawValue: String) =
  Phone(rawValue = rawValue, number = number, type = PhoneType.values().getOrElse(type) { PhoneType.UNKNOWN })

private fun EmailParcelable.toEmail(rawValue: String) =
  Email(
    rawValue = rawValue,
    address = address,
    body = body,
    subject = subject,
    type = EmailType.values().getOrElse(type) { EmailType.UNKNOWN }
  )

private fun AddressParcelable.toAddress() =
  Address(addressLines = addressLines, type = AddressType.values().getOrElse(type) { AddressType.UNKNOWN })

private fun PersonNameParcelable.toPersonName() =
  PersonName(
    first = first,
    formattedName = formattedName,
    last = last,
    middle = middle,
    prefix = prefix,
    pronunciation = pronunciation,
    suffix = suffix
  )

private fun CalendarDateTimeParcelable.toCalendarEvent() =
  CalendarDateTime(
    day = day,
    hours = hours,
    minutes = minutes,
    month = month,
    seconds = seconds,
    year = year,
    utc = utc
  )