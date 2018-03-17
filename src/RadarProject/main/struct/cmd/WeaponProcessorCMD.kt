@file:Suppress("NAME_SHADOWING")
package main.struct.cmd

import main.deserializer.ROLE_MAX
import main.deserializer.channel.ActorChannel.Companion.actorHasWeapons
import main.deserializer.channel.ActorChannel.Companion.actors
import main.struct.*
import main.util.DynamicArray

object WeaponProcessorCMD {
    fun process(actor: Actor, bunch: Bunch, repObj: NetGuidCacheObject?, waitingHandle: Int, data: HashMap<String, Any?>): Boolean {
        with(bunch) {
            when (waitingHandle) {
            //AActor
                1 -> if (readBit()) {//bHidden
                }
                2 -> if (!readBit()) {// bReplicateMovement
                }
                3 -> if (readBit()) {//bTearOff
                }
                4 -> {
                    val role = readInt(ROLE_MAX)
                    val b = role
                }
                5 -> {
                    val (netGUID, _) = readObject()
                    actor.owner = if (netGUID.isValid()) netGUID else null
//          println("$actor isOwnedBy ${ActorChannel.actors[netGUID] ?: netGUID}")
                }
                6 -> {
                    repMovement(actor)
                }
                7 -> {
                    val (a, _) = readObject()
                    val attachTo = if (a.isValid()) {
                        actors[a]?.attachChildren?.put(actor.netGUID, actor.netGUID)
                        a
                    } else null
//          println("$actor attachedTo ${ActorChannel.actors[a] ?: a}")
                    if (actor.attachParent != null)
                        actors[actor.attachParent!!]?.attachChildren?.remove(actor.netGUID)
                    actor.attachParent = attachTo
                }
                8 -> propertyVector100()
                9 -> propertyVector100()
                10 -> readRotationShort()
                11 -> propertyName()
                12 -> readObject()
                13 -> readInt(ROLE_MAX)
                14 -> propertyBool()
                15 -> propertyObject()
            //AWeaponProcessor
                16 -> {//EquippedWeapons
                    val arraySize = readUInt16()
                    actorHasWeapons.compute(actor.owner!!) { _, equippedWeapons ->
                        val equippedWeapons = equippedWeapons?.resize(arraySize) ?: DynamicArray(arraySize)
                        var index = readIntPacked()
                        while (index != 0) {
                            val i = index - 1
                            val (netguid, _) = readObject()
                            equippedWeapons[i] = netguid
                            index = readIntPacked()
                        }
                        equippedWeapons
                    }
                }
                17 -> {//CurrentWeaponIndex
                    val currentWeaponIndex = propertyInt()
//          println("$actor carry $currentWeaponIndex")
//          val a = currentWeaponIndex
                }
                else -> return false
            }
            return true
        }
        return true
    }
}