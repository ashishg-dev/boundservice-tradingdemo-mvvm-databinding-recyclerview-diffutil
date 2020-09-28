package com.devashish.machinetest.enum

sealed class HttpStatus {
    object SUCCESS : HttpStatus()
    object ERROR : HttpStatus()
    object LOADING : HttpStatus()
}