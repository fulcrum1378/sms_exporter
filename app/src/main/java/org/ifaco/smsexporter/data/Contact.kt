package org.ifaco.smsexporter.data

data class Contact(
    var name: String,
    var numbers: ArrayList<String>,
    var photo: String?
) {
    companion object {
        fun findContactByName(list: List<Contact>, name: String): Contact? {
            var contact: Contact? = null
            for (con in list) if (con.name == name) contact = con
            return contact
        }

        fun findContactByPhone(list: List<Contact>, number: String): Contact? {
            var contact: Contact? = null
            for (con in list) if (number in con.numbers) contact = con
            return contact
        }
    }
}
