package kg.ivy.data.model.sync

interface Identifiable<ID : UniqueId> {
    val id: ID
}
