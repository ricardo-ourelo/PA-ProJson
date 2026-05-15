package projjson.annotations

/**
 * Ativa serialização por referência.
 *
 * Permite gerar:
 * - $id
 * - $ref
 *
 * evitando duplicação e ciclos infinitos.
 */

@Target(AnnotationTarget.PROPERTY)
annotation class Reference