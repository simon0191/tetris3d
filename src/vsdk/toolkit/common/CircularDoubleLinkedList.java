//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - January 3 2007 - Oscar Chavarro: Original base version                =
//===========================================================================

package vsdk.toolkit.common;

class _CircularDoubleLinkedListNode<E> extends FundamentalEntity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20070422L;

    public E data;
    public _CircularDoubleLinkedListNode<E> next;
    public _CircularDoubleLinkedListNode<E> previous;
}

public class CircularDoubleLinkedList<E> extends FundamentalEntity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20070422L;

    private _CircularDoubleLinkedListNode<E> head;
    private _CircularDoubleLinkedListNode<E> window;
    private int lastAccessedIndex;
    private int currentSize;

    public CircularDoubleLinkedList()
    {
        head = null;
        window = null;
        currentSize = 0;
        lastAccessedIndex = -1;
    }

    public int size()
    {
        return currentSize;
    }

    public void add(E e)
    {
        _CircularDoubleLinkedListNode<E> newContainer;
        newContainer = new _CircularDoubleLinkedListNode<E>();
        newContainer.data = e;
        if ( head == null ) {
            head = newContainer;
            newContainer.next = newContainer;
            newContainer.previous = newContainer;
        }
        else {
            newContainer.previous = head.previous;
            newContainer.next = head;
            head.previous.next = newContainer;
            head.previous = newContainer;
        }
        currentSize++;
    }

    public void insertBefore(E newElem, E pivot)
    {
        locateWindowAtElem(pivot);
        _CircularDoubleLinkedListNode<E> newContainer;

        newContainer = new _CircularDoubleLinkedListNode<E>();
        newContainer.data = newElem;
        lastAccessedIndex = -1;

        if ( head == null ) {
            head = newContainer;
            newContainer.next = newContainer;
            newContainer.previous = newContainer;
        }
        else if ( window == null || window == head ) {
            window = head;
            head = newContainer;
            newContainer.previous = window.previous;
            newContainer.next = window;
            window.previous.next = newContainer;
            window.previous = newContainer;
        }
        else {
            newContainer.previous = window.previous;
            newContainer.next = window;
            window.previous.next = newContainer;
            window.previous = newContainer;
        }
        currentSize++;
    }

    public void locateWindowAtIndex(int index)
    {
        if ( index < 0 || index >= currentSize ) {
            return;
        }
        int i;
        for ( i = 0, window = head;
              i < currentSize && i < index;
              i++, window = window.next );
        lastAccessedIndex = i;
    }

    public boolean locateWindowAtElem(E e)
    {
        int i;

        lastAccessedIndex = -1;
        for ( i = 0, window = head;
              i < currentSize;
              i++, window = window.next ) {
            if ( window.data == e ) {
                lastAccessedIndex = i;
                return true;
            }
        }
        window = null;
        return false;
    }

    public void swapElements(E e1, E e2)
    {
        locateWindowAtElem(e1);
        _CircularDoubleLinkedListNode<E> window1 = window;
        locateWindowAtElem(e2);
        _CircularDoubleLinkedListNode<E> window2 = window;

        if ( window1 == null || window2 == null ) return;
        E temp = window1.data;

        window1.data = window2.data;
        window2.data = temp;
    }

    public E next()
    {
        lastAccessedIndex = -1;
        if ( window == null ) {
            window = head;
        }
        E elem = window.data;
        window = window.next;
        return elem;
    }

    public E getWindow()
    {
        if ( head == null ) return null;
        lastAccessedIndex = -1;
        if ( window == null ) {
            window = head;
        }
        return window.data;
    }

    public E previous()
    {
        lastAccessedIndex = -1;
        if ( window == null ) {
            window = head;
        }
        E elem = window.data;
        window = window.previous;
        return elem;
    }

    public E get(int index)
    {
        if ( index < 0 || index >= currentSize ) {
            // Report index out of bounds exception!
            String msg;
            msg = "IndexOutOfBounds Exception! - Trying to `get` with index " + index + " in a list with " + currentSize + " elements.";

            VSDK.reportMessage(this, VSDK.FATAL_ERROR, "get", msg);
            return null;
        }
        if ( lastAccessedIndex >= 0 && lastAccessedIndex == (index-1) &&
             lastAccessedIndex < (currentSize-1) ) {
            lastAccessedIndex++;
            window = window.next;
            return window.data;
        }
        int i;
        for ( i = 0, window = head;
              i < currentSize && i < index; i++, window = window.next );
        lastAccessedIndex = i;
        return window.data;
    }

    public void remove(int pos)
    {
        locateWindowAtIndex(pos);
        removeElemAtWindow();
    }

    public void removeElemAtWindow()
    {
        if ( window == null ) return;
        if ( window == head ) head = window.next;
        window.previous.next = window.next;
        window.next.previous = window.previous;
        window = null;
        currentSize--;
    }

    public void push(E newElem)
    {
        window = head;
        lastAccessedIndex = 0;
        _CircularDoubleLinkedListNode<E> newContainer;

        newContainer = new _CircularDoubleLinkedListNode<E>();
        newContainer.data = newElem;
        lastAccessedIndex = -1;

        if ( head == null ) {
            head = newContainer;
            newContainer.next = newContainer;
            newContainer.previous = newContainer;
        }
        else if ( window == null || window == head ) {
            window = head;
            head = newContainer;
            newContainer.previous = window.previous;
            newContainer.next = window;
            window.previous.next = newContainer;
            window.previous = newContainer;
        }
        else {
            newContainer.previous = window.previous;
            newContainer.next = window;
            window.previous.next = newContainer;
            window.previous = newContainer;
        }
        currentSize++;
    }

    public void reverse()
    {
        _CircularDoubleLinkedListNode<E> ptr, qtr;
        E tmp;
        int i = 0;

        ptr = head;
        qtr = head.previous;
        do {
            tmp = ptr.data;
            ptr.data = qtr.data;
            qtr.data = tmp;

            ptr = ptr.next;
            qtr = qtr.previous;
            i++;
        } while ( ptr != head && i < currentSize/2 );
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
