package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.remote.PullResponse

interface PullRepository {
    suspend fun pullData(): Result<Unit>
}
