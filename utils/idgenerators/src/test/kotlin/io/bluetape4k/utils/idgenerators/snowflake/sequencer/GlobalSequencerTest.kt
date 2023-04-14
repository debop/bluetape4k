package io.bluetape4k.utils.idgenerators.snowflake.sequencer

class GlobalSequencerTest : AbstractSequencerTest() {

    override val sequencer: Sequencer = GlobalSequencer()
}
