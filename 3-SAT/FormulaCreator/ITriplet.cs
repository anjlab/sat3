namespace FormulaCreator
{
    public interface ITriplet
    {
        /// <summary>
        /// One-based index of first variable in the triplet.
        /// </summary>
        int AName { get; }
        int BName { get; }
        int CName { get; }
        /// <summary>
        /// Value <code>true</code> means the variable is with logical NOT.
        /// </summary>
        bool NotA { get; }
        bool NotB { get; }
        bool NotC { get; }

        string CanonicalName { get; }
        bool HasSameVariablesAs(ITriplet triplet);
        ITriplet TransformAs(ITriplet triplet);
        bool HasVariable(int varName);
    }
}