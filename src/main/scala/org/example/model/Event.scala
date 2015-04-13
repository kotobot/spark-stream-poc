package org.example.model

import java.util.Date

sealed trait Message {
  def timestamp: Date
}

case class Event(timestamp: Date) extends Message