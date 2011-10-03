package net.godcode.hazelbox

import com.mongodb.casbah.Imports.ObjectId

case class Person(_id: ObjectId = new ObjectId(), firstName: String)
