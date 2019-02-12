package com.example.utils.data.yv

data class YVVotdResponse(val data : Array<VotdRef>) {
    data class VotdRef (val references : Array<String>)
}