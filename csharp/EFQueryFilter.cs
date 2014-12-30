public static IQueryable<T> Filter<T>( this IQueryable<T> queryable, Expression<Func<T, bool>> predicate ) {
    predicate.CheckNull( "predicate" );
    if ( Lambda.GetCriteriaCount( predicate ) > 1 )
        throw new InvalidOperationException( String.Format( "仅允许添加一个条件,条件：{0}", predicate ) );
    if ( predicate.Value() == null )
        return queryable;
    if ( string.IsNullOrWhiteSpace( predicate.Value().ToString() ) )
        return queryable;
    return queryable.Where( predicate );
}
