package com.asp424.drawer.utilites

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class AppChildEventListener (val OnSuccess:(DataSnapshot) ->Unit): ChildEventListener{
    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
       // TODO("Not yet implemented")
OnSuccess(p0)
    }
    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
    }
    override fun onChildRemoved(snapshot: DataSnapshot) {
        //TODO("Not yet implemented")
    }
    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        //TODO("Not yet implemented")
    }
    override fun onCancelled(error: DatabaseError) {
        //TODO("Not yet implemented")
    }
}