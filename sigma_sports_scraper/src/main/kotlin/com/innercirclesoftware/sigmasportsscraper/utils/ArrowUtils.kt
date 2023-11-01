package com.innercirclesoftware.sigmasportsscraper.utils

import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.some

val <L, R> Ior<L, R>.right: Option<R>
    get() {
        return fold(
                fa = { None },
                fb = { it.some() },
                fab = { _, right -> right.some() }
        )
    }

val <L, R> Ior<L, R>.left: Option<L>
    get() {
        return fold(
                fa = { it.some() },
                fb = { None },
                fab = { left, _ -> left.some() }
        )
    }